#!/bin/bash

# Указание имени результирующего файла
output_file="result.txt"

# Очистить содержимое результирующего файла, если он существует
> "$output_file"

# Найти и объединить файлы с указанными расширениями и именем pom.xml
find . \( \
  -name "*.java" -o \
  -name "*.html" -o \
  -name "*.properties" -o \
  -name "*.yaml" -o \
  -name "pom.xml" \
\) -type f | while read -r file; do
  cat "$file" >> "$output_file"
  echo -e "\n" >> "$output_file" # Добавить пустую строку как разделитель
done

echo "Содержимое файлов объединено в $output_file"
