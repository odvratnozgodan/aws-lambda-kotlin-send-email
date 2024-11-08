# Send emails with AWS Lambda, Kotlin and AWS SES


## Motivation
Sometimes you need to send emails programmatically. This can be a simple contact form on your website, verification 
emails during account registration flow or just notification to users from your web app. Instead of setting up your 
SMTP server you can use AWS Lambda functions and AWS SES(Simple Email Service). 
I will use it to send emails from a contact form I included on my website, but the code can easily be adjusted to suite your needs.
This project is the second in the series and builds upon the [first one](https://github.com/odvratnozgodan/aws-lambda-kotlin-basic).

## Prerequisites:
- AWS account
- aws-cli installed and configured for your account
- Configure AWS SES. This can be done in the following way:
    1. In the AWS console navigate to AWS SES.
    2. Create SES configuration set by following [this instructions](https://docs.aws.amazon.com/ses/latest/dg/managing-configuration-sets.html#console-detail-configuration-sets), you will need this for the next step.
    3. Add and verify your email address easiest to do through AWS CLI. Replace the `SENDER-EMAIL-ADDRESS` and `CONFIG-SET`
    ```bash
    aws sesv2 create-email-identity --email-identity SENDER-EMAIL-ADDRESS --configuration-set-name CONFIG-SET
    ```
    4. For testing you can skip the verification of the sending domain.
    5. If you use Gmail you can use the same email for the sender and receiver, but the receiver email must have a wildcard(eg. sender: myemail@gmail.com, receiver:myemail+1@gmail.com)

## Steps:
1. Checkout the project project
2. Rename the file `template.secret.properties` to `secret.properties` and inside it change the sender email and receiver email(the sender email is the one that you verified through AWS SES).

## Deployment:
1. Create a S3 bucket where the final template will be stored
```bash
aws s3 mb s3://lambda-kotlin-send-email
```
2. Generate the final template
```bash
aws cloudformation package --template-file template.yml --s3-bucket lambda-kotlin-send-email --output-template-file template-out.yml
```
3. Deploy the final template
```bash
aws cloudformation deploy --template-file template-out.yml --stack-name lambda-kotlin-send-email --capabilities CAPABILITY_NAMED_IAM
```
4. To test execute the following command. Note that you have to change the `YOUR_FUNCTION_NAME` to the actual name of your Lambda function(you can find it in the AWS Lambda dashboard):
```
aws lambda invoke --function-name YOUR_FUNCTION_NAME --payload '{ "firstName": "John", "lastName": "Doe", "email":"sender.email@example.com", "message":"Hey! Ho! Lets go!" }' out.json
```

## Closing notes
This Lambda function can only be called through the AWS CLI, if you want to make it publicly accessible you can do so by 
opening the AWS Lambda dashboard, finding the Lambda function and configure it's **Function URL**. Take care when 
doing this, if you make the function fully public you have to take care of the authentication on your own. You don't want
someone to exploit the function URL and rack up your AWS bill 💸💸.