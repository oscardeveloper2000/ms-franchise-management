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