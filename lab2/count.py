import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter

schema = avro.schema.parse(open("country.avsc").read())
reader = DataFileReader(open("countries.avro", "r"), DatumReader())

dict_list = []
for user in reader:
    dict_list = dict_list + [user]
reader.close()

df = pd.DataFrame(dict_list)
print "# of Countries with population > 10000000: " + str(len(df[df.population > 10000000].index))
