#!/usr/bin/python3
import requests

r = requests.post('https://ipsc.ksp.sk/2018/practice/problems/r1', cookies={'ipscsessid': 'aaf5762478c617d4ea778e1cb622350bdf474520'})
print(r.status_code, r.reason, r.text)
