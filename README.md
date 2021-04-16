1) Run "sbt test" to run tests


2) Run "sbt run" and POST bid requests to http://127.0.0.1/request-bids

---

Example command to POST bid request with the help of cURL command-line tool:

<pre>
curl -0 -v http://localhost:8080/request-bids \
-H 'Content-Type:application/json;charset=utf-8' \
-d @- <<'EOF'
{
  "id": "SGu1Jpq1IO",
  "site": {
	"id": "0006a522ce0f4bbbbaa6b3c38cafaa0f",
	"domain": "fake.tld"
  },
  "device": {
	"id": "440579f4b408831516ebd02f6e1c31b4",
	"geo": {
  	"country": "LT"
	}
  },
  "imp": [
	{
  	"id": "1",
  	"wmin": 50,
  	"wmax": 300,
  	"hmin": 100,
  	"hmax": 300,
  	"h": 250,
  	"w": 300,
  	"bidFloor": 3.12123
	}
  ],
  "user": {
	"geo": {
  	"country": "LT"
	},
	"id": "USARIO1"
  }
}
EOF
</pre>

---

Expected response:

<pre>
{"adid":"1","banner":{"height":250,"id":1,"src":"https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg","width":300},"bidRequestId":"SGu1Jpq1IO","id":"response1","price":3.12123}
</pre>