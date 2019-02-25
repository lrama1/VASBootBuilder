const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const jsonQuery = require('json-query')

const app = express()
app.use(cors())
app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())

const ${domainClassName.toLowerCase()}s = require('./${domainClassName}s.json')

app.get('/${projectName}/${domainClassName.toLowerCase()}s', (req, res) =>{
    return res.json(${domainClassName.toLowerCase()}s)
})

app.get('/${projectName}/${domainClassName.toLowerCase()}/:${domainClassIdAttributeName}', (req, res) =>{
    const returnVal = jsonQuery('rows[${domainClassIdAttributeName}=' + req.params.${domainClassIdAttributeName} + ']',{data: ${domainObjectName}s})
    return res.json(returnVal.value)
})

app.listen(8000)
console.log('Listening on port 8000')