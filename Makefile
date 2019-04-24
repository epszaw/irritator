run-bot:
	lein with-profile bot run

run-daemon:
	lein with-profile daemon run
	
compile:
	rm -rf target
	lein with-profile bot:daemon uberjar
	cp config.yml target

compile-bot:
	rm -rf target
	lein with-profile bot uberjar
	cp config.yml target

compile-daemon:
	rm -rf target
	lein with-profile daemon uberjar
	cp config.yml target
	