run-bot:
	lein with-profile bot run

run-daemon:
	lein with-profile daemon run
	
compile:
	rm -rf target

	lein with-profile bot:daemon uberjar
	
	cp -rf resources target
	cp config.yml target
