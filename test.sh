#!/bin/bash

# Automate test

curl -sH "Content-Type: application/json" -X POST -d 'data:text/plain;base64,c29tZSBzdHJpbmc=' http://localhost:8080/v1/diff/1/left/ > /dev/null
curl -sH "Content-Type: application/json" -X POST -d 'data:text/plain;base64,c29tZSBzdHJpbmc=' http://localhost:8080/v1/diff/1/right/ > /dev/null

# output: Equal
curl http://localhost:8080/v1/diff/1
echo -e "\n###"

curl -sH "Content-Type: application/json" -X POST -d 'data:text/plain;base64,c29tZSBzdHJpbmc=' http://localhost:8080/v1/diff/1/left/ > /dev/null
curl -sH "Content-Type: application/json" -X POST -d 'data:text/plain;base64,a29tZSBxdHJpbmc=' http://localhost:8080/v1/diff/1/right/ > /dev/null

# output: Same size (Left file:11 bytes / Right file: 11 bytes), but different content. Offsets/Length: 0/1, 4/1.
curl http://localhost:8080/v1/diff/1
echo -e "\n###"

green_group_base64=$(cat "src/test/resources/greengroup.txt")
curl -sH "Content-Type: application/json" -X POST -d "${green_group_base64}" http://localhost:8080/v1/diff/1/left/ > /dev/null
curl -sH "Content-Type: application/json" -X POST -d 'data:text/plain;base64,a29tZSBxdHJpbmc=' http://localhost:8080/v1/diff/1/right/ > /dev/null

# output : Not equal. Left file: 25627 bytes.Right file: 11 bytes
curl http://localhost:8080/v1/diff/1
echo -e "\n###"

github_base64=$(cat "src/test/resources/github.txt")
github2_base64=$(cat "src/test/resources/github2.txt")
curl -sH "Content-Type: application/json" -X POST -d "${github_base64}" http://localhost:8080/v1/diff/1/left/ > /dev/null
curl -sH "Content-Type: application/json" -X POST -d "${github2_base64}" http://localhost:8080/v1/diff/1/right/ > /dev/null

# output: Same size (Left file:7157 bytes / Right file: 7157 bytes), but different content. Offsets/Length: 6697/1, 7155/1
curl http://localhost:8080/v1/diff/1
echo -e "\n###"

curl -sH "Content-Type: application/json" -X POST -d "${github_base64}" http://localhost:8080/v1/diff/1/left/ > /dev/null
curl -sH "Content-Type: application/json" -X POST -d "${github_base64}" http://localhost:8080/v1/diff/1/right/ > /dev/null

# output: Equal
curl http://localhost:8080/v1/diff/1
echo -e "\n###"