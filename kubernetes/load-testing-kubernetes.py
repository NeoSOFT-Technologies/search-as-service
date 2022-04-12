
# import requests module
import requests
import json
from faker import Faker
import random
import time

fake = Faker()

print("YO")

data=[]

for i in range(0,10):

      newEntry={
            "name":fake.name(),
            "age":random.randint(10, 100),
            "surname":fake.name(),
            "father_name":fake.name(),
            "mother_name":fake.name(),
      }

      data.append(newEntry)

 
while True:
      # response = requests.get('http://172.25.223.121:30510/solr/test_collection/select?indent=true&q.op=OR&q=*%3A*&rows=200')
      # print(response)
      # print(response.json())

      # time.sleep(0.5)

      headers = {"Content-Type": "application/json"}

      print("Sending Request")
      response = requests.post("http://172.25.222.78:31632/solr/collection_test/update?_=1648551043551&commitWithin=100&overwrite=true&wt=json", headers=headers,  data=json.dumps(data))
      print("JSON Response ", response.json())