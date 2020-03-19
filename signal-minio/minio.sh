#!/bin/bash

# this script is used to test the minio docker

# usage: chmod +x ./minio.sh
# usage: ./minio.sh my-bucket my-file.zip

bucket=$1
file=$2

host=domain.com:9000
s3_key='Q3AM3UQ867SPQQA43P2F'
s3_secret='zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG'

base_file=`basename ${file}`
resource="/${bucket}/${base_file}"
content_type="application/octet-stream"
date=`date -R`
_signature="PUT\n\n${content_type}\n${date}\n${resource}"
signature=`echo -en ${_signature} | openssl sha1 -hmac ${s3_secret} -binary | base64`

curl -v -X PUT -T "${file}" \
          -H "Host: $host" \
          -H "Date: ${date}" \
          -H "Content-Type: ${content_type}" \
          -H "Authorization: AWS ${s3_key}:${signature}" \
          https://$host${resource}
