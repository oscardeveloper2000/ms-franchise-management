#!/usr/bin/env bash

set -euo pipefail

cluster="franquicias-api"
service="franquicias-api"

task_arn=$(aws ecs list-tasks \
  --cluster "$cluster" \
  --service-name "$service" \
  --desired-status RUNNING \
  --query 'taskArns[0]' \
  --output text)

if [[ -z "$task_arn" || "$task_arn" == "None" ]]; then
  echo "No hay ninguna tarea RUNNING para el servicio '$service' en el cluster '$cluster'." >&2
  exit 1
fi

eni_id=$(aws ecs describe-tasks \
  --cluster "$cluster" \
  --tasks "$task_arn" \
  --query "tasks[0].attachments[].details[?name=='networkInterfaceId'].value | [0]" \
  --output text)

if [[ -z "$eni_id" || "$eni_id" == "None" ]]; then
  echo "No se encontró la ENI de la tarea '$task_arn'." >&2
  exit 1
fi

public_ip=$(aws ec2 describe-network-interfaces \
  --network-interface-ids "$eni_id" \
  --query 'NetworkInterfaces[0].Association.PublicIp' \
  --output text)

if [[ -z "$public_ip" || "$public_ip" == "None" ]]; then
  echo "La ENI '$eni_id' no tiene una IP pública asociada." >&2
  exit 1
fi

printf 'http://%s:8080\n' "$public_ip"