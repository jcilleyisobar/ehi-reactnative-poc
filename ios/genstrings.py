#!/usr/bin/env python3

import os
import sys
import fnmatch
from optparse import OptionParser

def main():
  global options

  # parse the arguments
  options = parse_args()

  # find the files to search for strings in
  files   = find_files(options.source_dir)
  strings = find_strings(files)
  
  options.serializer.serialize(strings)

##
## Search/Serialization
##

def find_files(directory):
  files = []

  # pull out all the .m files in the directory or its children 
  for root, dirnames, filenames in os.walk(directory):
    for filename in fnmatch.filter(filenames, '*.m'):
      files.append(os.path.join(root, filename))
 
  return files

def find_strings(source_files):
  # pull out all the strings from each file
  strings = [ string 
    for source_file in source_files 
    for string in find_strings_in_file(source_file) ]

  # filter and report duplicate strings
  string_map = dict()
  for string in strings:
    if not string.key in string_map:
      string_map[string.key] = string
    else:
      log.debug('ignoring duplicate: ' + string.key)

  # sort the strings by key
  strings = sorted(string_map.values(), key=lambda x: x.key)

  # log the files, maybe
  log.debug(string_from_slice('Files', source_files, 10))
  # log the strings, maybe
  log.debug(string_from_slice('Strings', strings, 10));

  return strings

def find_strings_in_file(source_file):
  strings = []
  line_number = 0

  for line in open(source_file):
    line_number += 1
    function_start = line.find(options.function)
    
    # if there's no string start, this line is clean
    if function_start != -1:
      invocation = line[function_start:]
      
      # try and create a string, and add it to the list if successful
      try:   
        localizable = LocalizableString(invocation)
      except Exception:
        raise
      else:
        strings.append(localizable)

  return strings

##
## Serialization
##

class Serializer:

  @classmethod
  def from_string(self, string):
    if(string is None):
      return StringsSerializer() 
    elif(string == 'json'):
      return JsonSerializer()
    else: 
      return None

  def serialize(self, strings):
    # make the directory if it doesn't exist
    directory = options.output_dir
    if directory is not None and not os.path.exists(directory):
      os.makedirs(directory) 

    # create the output file(s)
    output_path = (directory or '') + self.filename()
    destination = open(output_path, 'w+')

    # allow subclass to do its thing
    self.serialize_to_file(strings, destination)
    
    # clean up after ourselves
    destination.close()

  def serialize_to_file(self, strings, destination):
    raise NotImplementedError('serializers must implement `serialize`')

  def filename(self):
    raise NotImplementedError('serializers must implement `filename`') 

class StringsSerializer(Serializer):

  def serialize_to_file(self, strings, destination):
    for string in strings:
      destination.write(self.serialize_string(string) + '\n\n')
  
  def serialize_string(self, string):
    return '/* %(comment)s */\n"%(key)s" = "%(default)s"' % string.__dict__

  def filename(self):
    return 'Localizable.strings'

class JsonSerializer(Serializer):

  def serialize_to_file(self, strings, destination):
     destination.write('{\n')

     last_string_index = len(strings) - 1
     for index, string in enumerate(strings):
       line = '\t' + self.serialize_string(string)
       line_ending = '\n' if index == last_string_index else ',\n'
       destination.write(line + line_ending)

     destination.write('}')

  def serialize_string(self, string):
    return '"%(key)s": "%(default)s"' % string.__dict__

  def filename(self):
    return 'localizable.en_VI.json'

##
## String Model
##

class LocalizableString:

  def __init__(self, function_start):
    # parse out our properties from the function 
    (self.key, self.default, self.comment) = self.parse_function(function_start)
    # otherwise, throw a parsing error
    if self.key is None or self.default is None or self.comment is None:
      raise Informative('Failed to parse %(function_start)s' % locals())

  def parse_function(self, function_start):
    function_end = function_start.rfind(')')

    if function_end != -1:
      # convert the invocation into a parameter list
      invocation = function_start[len(options.function) + 1 : function_end]
      parameters = [ self.clean(string) for string in invocation.split('@"') if len(string) > 0 ]

      # ensure we have the right number of parameters
      if len(parameters) == 3:
        return (parameters[0], parameters[1], parameters[2])

    return (None, None, None)

  def clean(self, string): 
    # remove any whitepace and the trailing comma
    string = string.rstrip(', ')
    # remove the trailing quote
    return string[:-1]

  def __str__(self):
    return self.key

##
## Options
##

def parse_args():
  # construct the parser
  parser = OptionParser("usage: %prog [options] source-dir")
  
  parser.add_option('-f', dest='function',
    help='The name of the localizing function/macro')
  parser.add_option('-o', dest='output_dir',
    help='The directory to write the output files to')
  parser.add_option('-t', dest='output_format',
    help='The output format [strings (default), json]')
  parser.add_option('-v', '--verbose', dest='debug', default=False, action="store_true",
    help='Logs more stuff to the screen')

  # parse out the options
  (options, args) = parser.parse_args()

  # validate the options/args we have
  if options.function is None:
    raise Informative('You must specify a function/macro name')
  if len(args) != 1:
    raise Informative('You must specify a source dir') 

  # populate flags
  global debug
  debug = options.debug != False

  # populate the positional args
  options.source_dir = args[0]
  options.output_dir = sanitize_directory(options.output_dir)

  # populate any convertible options
  options.serializer = Serializer.from_string(options.output_format)
  if options.serializer is None:
    raise Informative(options.output_format + ' is not a valid output format')

  return options 

def parse_format(output_format):
  pass

def sanitize_directory(directory):
  if directory is not None and not directory.endswith('/'):
    return directory + '/'
  return directory

##
## Logging 
##

class log:
  ERROR  = '\033[91m'
  NORMAL = '\033[0m'

  @classmethod 
  def info(self, value):
    print(str(value))

  @classmethod
  def debug(self, value):
    if debug:
      print(str(value));

  @classmethod 
  def error(self, value):
    print(log.ERROR + '[✗] ' + str(value) + log.NORMAL)

def string_from_slice(label, items, length):
  # declare locals for formatting 
  chunk = '\n'.join(str(item) for item in items[:length])
  count = len(items)
  return "%(label)s (%(count)d total):\n%(chunk)s" % locals()

class Informative(Exception):
  pass

##
## Start 
##

try:
  main()
except Informative as exception:
  log.error(exception)

