import sys;
import re;
def main():
	f = open(sys.argv[1]);
	r = re.compile(r'\s+def\s+baseVersion\s+=\s+"([^"]+)".*');
	for i in f:
		if(r.match(i)):
			print r.match(i).group(1);
			f.close();
			return ;
	f.close();
main();