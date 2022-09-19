NAME = dcm4ceph
DIST = $(NAME)-dist
ROOT = ./
WEBSITE = afm@noise.brillig.org:public_html/antoniomagni.org/$(NAME)/download/
PACKAGES = $(ROOT)$(DIST)/target/*.zip $(ROOT)$(DIST)/target/*.tar* $(ROOT)/target/*.zip* $(ROOT)/target/*.tar*
MVN = $(PWD)/maven/bin/mvn

.PHONY: clean src build deploy

clean:
	find . -path "*/target/*" -delete
	find . -type d -name "target" -delete
	$(MVN) clean
	$(MVN) -f $(DIST) clean
	echo "Cleaned all targets."

src:
	echo "Building src packages..."
	$(MVN) assembly:assembly

build:
	echo "Building..."
	$(MVN) install

dist:
	echo "Build distribution binaries"
	$(MVN) -f $(DIST) assembly:assembly -P bin

deploy:
	echo "Deploying"
	rsync -auv "$(PACKAGES)" "$(WEBSITE)"


