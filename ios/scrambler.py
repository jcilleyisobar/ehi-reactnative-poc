#!/usr/bin/env python3

import sys
import re
import random
import json 

def main():
  path   = sys.argv[1]
  source = sys.argv[2]
  dest   = sys.argv[3]

  source_path = path + '/' + source
  dest_path   = path + '/' + dest

  with open(source_path, "r") as localizations:
    # parse each line
    text = localizations.read().encode('utf_8').decode('unicode_escape')
    matches = re.finditer('\"(.*)\": \"(.*)\",', text, re.MULTILINE)

    result = dict()
   
    # enumerate the matches
    for match in matches:
      # get the substring
      key_span = match.span(1)
      str_span = match.span(2)
      
      key    = text[key_span[0] : key_span[1]]
      string = text[str_span[0] : str_span[1]]

      # scramble it
      characters = list(string)
      random.shuffle(characters)

      word = ''.join(characters)
      result[key] = word

    with open(dest_path, "w") as output_file:
      output_file.write(json.dumps(result))
      print("wrote result")

main()

