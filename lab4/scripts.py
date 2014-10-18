import re

file_name = "cmsc.txt"

with open(file_name) as f:
    lines = f.readlines()

num_lines = len(lines)
i = 0
while i < num_lines:
	l = lines[i]
	if re.match('^CMSC[0-9]{3}', l):
		class_name = l.rstrip()
		info = class_name + ", "

		while(i + 1 < num_lines and re.match('^[0-9]{4}', lines[i+1].rstrip())):
			section = lines[i+1].rstrip();
			prof_name = lines[i+2].rstrip()
			seats = lines[i+3].rstrip()
			days_time = lines[i+4].rstrip().split(" ")
			days = days_time[0]
			days_time = days_time[1] + " - " + days_time[3]

			building, room = lines[i+5].rstrip().split("  ")

			m_seats = re.match('\d+',seats)
			t, o, w = (seats[7:-1].split(", "))
			t, o, w = t.split(": ")[1], o.split(": ")[1], w.split(": ")[1]

			print(class_name + ", " + section + ", " + \
				prof_name + ", " + t + ", " + o + ", " + w + ", " + \
				days + "," + days_time + ", " + building + ", " + room)
			i = i + 5
	else:
		i += 1



