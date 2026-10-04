data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default_vpc" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

resource "aws_security_group" "app" {
  name        = "${var.app_name}-ecs"
  description = "Security group for the ECS Express application"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "Public HTTP traffic to the application"
    protocol    = "tcp"
    from_port   = 8080
    to_port     = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow outbound application traffic"
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.app_name}-ecs"
    App  = var.app_name
  }
}

data "aws_iam_policy_document" "ecs_execution_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "execution_role" {
  name               = "${var.app_name}-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_execution_assume_role.json

  tags = {
    Name = "${var.app_name}-execution-role"
    App  = var.app_name
  }
}

resource "aws_iam_role_policy_attachment" "execution_role" {
  role       = aws_iam_role.execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

data "aws_iam_policy_document" "ecs_infrastructure_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "infrastructure_role" {
  name               = "${var.app_name}-infrastructure-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_infrastructure_assume_role.json

  tags = {
    Name = "${var.app_name}-infrastructure-role"
    App  = var.app_name
  }
}

resource "aws_iam_role_policy_attachment" "infrastructure_role" {
  role       = aws_iam_role.infrastructure_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSInfrastructureRoleforExpressGatewayServices"
}

data "aws_iam_policy_document" "ecs_task_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "task_role" {
  name               = "${var.app_name}-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role.json

  tags = {
    Name = "${var.app_name}-task-role"
    App  = var.app_name
  }
}

resource "aws_iam_policy" "dynamodb_access" {
  name = "${var.app_name}-dynamodb-access"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem"
        ]
        Resource = aws_dynamodb_table.franquicias.arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "task_role_dynamodb_access" {
  role       = aws_iam_role.task_role.name
  policy_arn = aws_iam_policy.dynamodb_access.arn
}

resource "aws_ecs_express_gateway_service" "app" {
  service_name            = var.app_name
  execution_role_arn      = aws_iam_role.execution_role.arn
  infrastructure_role_arn = aws_iam_role.infrastructure_role.arn
  task_role_arn           = aws_iam_role.task_role.arn
  health_check_path       = "/health"
  cpu                     = "256"
  memory                  = "512"

  network_configuration = [{
    security_groups = [aws_security_group.app.id]
    subnets         = data.aws_subnets.default_vpc.ids
  }]

  primary_container {
    image          = "${aws_ecr_repository.app.repository_url}:latest"
    container_port = 8080

    environment {
      name  = "AWS_REGION"
      value = var.aws_region
    }

    environment {
      name  = "AWS_DYNAMODB_TABLE_NAME"
      value = var.dynamodb_table_name
    }
  }
}