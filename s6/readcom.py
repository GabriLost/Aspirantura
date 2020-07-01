import serial
import time
import re

from time import gmtime, strftime
import csv
Z19B_baudrate = 9600
Z19B_port = 'COM3'  # set the correct port before run it # COM3

ser = serial.Serial()
ser.port=Z19B_port
ser.baudrate=Z19B_baudrate
encoding = 'utf-8'
ser.timeout = 2 # what for?

ser.open()
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
            with open('ppm_data.csv', 'a', newline='') as f:
                writer = csv.writer(f, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
                writer.writerow(data)
        time.sleep(10)
else:
    print ('z1serial not open')

