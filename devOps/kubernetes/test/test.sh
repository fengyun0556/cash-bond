kubectl run test-curl --image=curlimages/curl -i --rm --restart=Never -- \
  curl --location 'http://trade-link/cashBond/order' \
  --header 'Content-Type: application/json' \
  --data '{
    "accountKey": "ABC12345678",
    "accountName": "李四的投资账户",
    "memberKey": "MEM98765432",
    "memberName": "李四",
    "cashAccount": "CASH666777888",
    "isin": "CN0378331005",
    "isinName": "公司名称",
    "exchangeCode": "NASDAQ",
    "orderType": "LIMIT",
    "quantity": 100,
    "price": 185.50,
    "orderRequestMode": "LIVE",
    "ruleCheckList": [
      {
        "ruleId": "RULE001",
        "ruleCheckResult": true,
        "ruleDescribe": "资金充足检查通过"
      },
      {
        "ruleId": "RULE002",
        "ruleCheckResult": true,
        "ruleDescribe": "持仓限制检查通过"
      }
    ]
  }'
