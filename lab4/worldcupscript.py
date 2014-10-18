import re
import fileinput

file_name = "worldcup.txt"
import fileinput
 
lines = []
for line in fileinput.input(file_name, inplace=False):
	line = re.sub('(\\|)+(\d)+(\\|)+.+', "", line.rstrip())
	line = re.sub('\\||{|}|fb|style=\".*\"', "", line.rstrip())
	line = re.sub('\\[|\\]', "", line.rstrip())
	line = re.sub('FIFA World Cup(\d){4}(#\d\\*)*', "", line.rstrip())
	line = re.sub('#\d\\^', "", line.rstrip())
	line = re.sub('(^style=.+)|-|<sup>...</sup>|\\(|\\)', "", line.rstrip())
	line = re.sub(' , ', " ", line.rstrip())
	lines.append(line)


num_lines = len(lines)
i = 0

while(not re.match('[A-Z]{3}', lines[i])):
	i += 1


while(i < num_lines and re.match('[A-Z]{3}', lines[i])):
	country = lines[i].rstrip()
	x = [1,2,3,4]
	for placing in x:
		l2 = lines[i+placing]
		if(not re.match('align=centersort dash', l2)):
			for year in l2.split(" "):
				if len(year) > 1:
					print (country + ", " + year + ", " + str(placing))
	i += 5

	while(i < num_lines and not re.match('[A-Z]{3}', lines[i])):
		i += 1
