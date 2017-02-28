#!/bin/bash
for file in $(find 575_json -type f | grep '.json')
do
 if [ $(grep -o "\<breast\>" $file | wc -l) -gt $(grep -o "\<lung\>" $file | wc -l) ]
 then
  echo "${file} is a breast-related report."
 else
  echo "${file} is a lung-related report."
 fi 
done
