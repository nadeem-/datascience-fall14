# create a 2 arrays with each having a set of duplicate records
# (id_a, id_b) is a duplicate

# first run through the tested duplicates
# then lookup each hash id starting with a number and not http in the 
# test result set
import numpy as np
import pandas as pd
from sets import Set

dedupe_results_file = 'products_out.csv'
testing_file = 'product_mapping.csv'

dedupe_results = pd.read_csv(dedupe_results_file, usecols=['Cluster ID', 'id', 'canonical_id'])
testing_results = pd.read_csv(testing_file)

# store all amazon to google mappings
correct_mappings = Set([])
for r_num, ids in testing_results.iterrows():
	amazon = ids[0]
	google = ids[1]
	correct_mappings.add(amazon + "" + google)

# Precision = tp / (tp + fp)
# Recall = tp / (tp + fn)
# check tp, fp, can't check fn
# fn = number in mappings that are not in dedupe results
tp = fp = fn = 0

clusters = Set([])
found_dups = Set([])

for r_num, ids in dedupe_results.iterrows():
	cluster_id = ids[0]
	src_id = ids[1]
	canon_id = ids[2]

	if(cluster_id in clusters):
		# found duplicate
		# check if correct match, if so tp, else fp
		found_dups.add(src_id + "" + canon_id)
		found_dups.add(canon_id + "" + src_id)

		if (src_id + "" + canon_id) in correct_mappings or (canon_id + "" + src_id) in correct_mappings:
			tp += 1
		else:
			fp += 1
	else:
		clusters.add(cluster_id)

for m in correct_mappings:
	if not m in found_dups:
		fn += 1

print "tp: " + str(tp)
print "fp: " + str(fp)
print "fn: " + str(fn)

precision = float(tp) / float(tp + fp)
recall =  float(tp) / float(tp + fn)

print "precision: " + str(precision)
print "recall: " + str(recall)