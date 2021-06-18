#!/usr/bin/env python3
# pip install pyjwt hashlib json time os sys ## just in case

import sys
import os
import jwt
import time
import json
import hashlib
import urllib.request
from urllib.error import URLError, HTTPError

usage=f"USAGE: KEY_ID=your_id KEY_SECRET=your_secret {sys.argv[0]} [--verbose|-v] file.json"

api='https://staging.aimmo.io/api/v2/partners/'

def die(message):
    print(message)
    sys.exit(1)

args = sys.argv

# args = ['dummy.py','-v','file.json']

argc = len(args)

if (argc<2
    or os.environ.get('KEY_ID') == None 
    or os.environ.get('KEY_SECRET') == None
    or argc>4):
    die(usage)

verbose = False

if argc == 3:
    flag = args[1] 
    if (flag == '-v' or flag == '--verbose'):
        verbose = True
    else:
        die(usage)
    
fileName = args[argc-1]

keyId = os.environ.get('KEY_ID')
keySecret = os.environ.get('KEY_SECRET')
jsonDoc = open(fileName, encoding='utf-8').read()
jsonBytes = jsonDoc.encode('utf-8');

claim = {
    "iat": str(int(time.time())),
    "iss": keyId,
    "sub": hashlib.sha256(jsonBytes).hexdigest()
}

if verbose:
    print(f"Claim:\n{claim}")

token = jwt.encode(payload=claim, key=keySecret, algorithm="HS256") 

headers = { 
    "Authorization" : f"Bearer {token}",
    "Content-Type" : "application/json; charset=utf-8"
}

request = urllib.request.Request(api,jsonBytes,headers)

if verbose:
    print(f"{request.get_method()} {request.full_url}")
    for key, value in request.header_items():
        print(f"{key}:{value}")
    print()
    print(jsonDoc)


try:
    response = urllib.request.urlopen(request)
except HTTPError as e:
    print('ERROR! The server couldn\'t fulfill the request.')
    print(f"{e.code} {e.reason}")
    print(e.info())
    print()
    print(e.read().decode())
except URLError as e:
    print('We failed to reach the server.')
    print('Reason: ', e.reason)
else:
    print(response.read().decode())
