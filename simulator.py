import socket
import subprocess
import sys
import time
import json
import requests 

def read_file(path):
    f = open(path, "r")
    if f.mode == "r":
        content = f.read()
        return content

matches = read_file("/Users/iortynskyi/fantastic-sport/src/main/resources/static/matches.json")
players = read_file("/Users/iortynskyi/fantastic-sport/src/main/resources/static/players.json")

matches_json = json.loads(matches)
players_json = json.loads(players)

MATHCES_ENDPOINT = "http://127.0.0.1:8080/v1/feeds/matches"
PLAYERS_ENDPOINT = "http://127.0.0.1:8080/v1/feeds/players"

headers = {'Content-Type': 'application/json', 'Accept':'application/json'}
  
matches_request = requests.post(url = MATHCES_ENDPOINT, data = json.dumps(matches_json), headers = headers) 

time.sleep(1)

players_request = requests.post(url = PLAYERS_ENDPOINT, data = json.dumps(players_json), headers = headers) 
  
print("Status code for matches:", matches_request.status_code, matches_request.reason)

print("Status code for players:", players_request.status_code, players_request.reason)

