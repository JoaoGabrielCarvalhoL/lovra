#!/bin/bash
set -euo pipefail

echo ">> Creating buckets for the development environment."

BUCKET_NAME="lovra-files"

echo ">> Creating buckets..."

awslocal s3 mb "s3://$BUCKET_NAME"
echo ">> Bucket $BUCKET_NAME created successfully!"

echo ">> Development environment configuration created successfully."