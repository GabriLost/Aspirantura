import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

sender_address = 'alexey.kaliberda@gmail.com'
sender_pass = 'aker6230'
recipients = ['parannoic@gmail.com']
#'snyatkovam.a@yandex.ru', 
mail_content = '''Hello there!'''
message = MIMEMultipart()
message['From'] = sender_address
message['To'] = ", ".join(recipients)
message['Subject'] = 'test with two recipients'   #The subject line
#The body and the attachments for the mail
message.attach(MIMEText(mail_content, 'plain'))

session = smtplib.SMTP('smtp.gmail.com', 587) #use gmail with port
session.starttls() #enable security
session.login(sender_address, sender_pass) #login with mail_id and password
text = message.as_string()
session.sendmail(sender_address, recipients, text)
session.quit()
print('Mail was sent')