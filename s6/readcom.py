import serial
import time
import re
import smtplib
import csv
from email.mime.multipart import MIMEMultipart
from email import encoders
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from time import gmtime, strftime

Z19B_baudrate = 9600
Z19B_port = '/dev/ttyUSB0'  # set the correct port before run it # COM3
filename = 'ppm_data.csv'
ser = serial.Serial()
ser.port=Z19B_port
ser.baudrate=Z19B_baudrate
encoding = 'utf-8'
ser.timeout = 2 # what for?

ser.open()

def sendmail():
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
    
    with open(filename, 'rb') as attachment:
        part = MIMEBase("application","octet-stream")
        part.set_payload(attachment.read())
    
    encoders.encode_base64(part)
    part.add_header(
        "Content-Disposition",
        "attachment",
        filename=filename)
    message.attach(part)

    session = smtplib.SMTP('smtp.gmail.com', 587) #use gmail with port
    session.starttls() #enable security
    session.login(sender_address, sender_pass) #login with mail_id and password
    text = message.as_string()
    session.sendmail(sender_address, recipients, text)
    session.quit()
    print('Mail was sent')
    
if ser.is_open:
    while True:
        size = ser.inWaiting()
        if size:
            raw_data = ser.read(size)
            raw_data = raw_data.decode(encoding)
            ppm = re.findall("\d+", raw_data)[0]
            t = re.findall("\d+", raw_data)[1]
            data = [strftime("%Y-%m-%d %H:%M:%S", gmtime()),ppm,t]
            print(data)
            with open(filename, 'a', newline='') as f:
                writer = csv.writer(f, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
                writer.writerow(data)
        time.sleep(60)
        if (strftime("%Y-%m-%d %H:%M:%S", gmtime()).find("17:00")>0):
            sendmail()
else:
    print ('z1serial not open')


    

            
