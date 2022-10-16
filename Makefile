NAME = dcm4ceph
DIST = $(NAME)-dist
ROOT = ./
WEBSITE = afm@noise.brillig.org:public_html/antoniomagni.org/$(NAME)/download/
PACKAGES = $(ROOT)$(DIST)/target/*.zip $(ROOT)$(DIST)/target/*.tar* $(ROOT)/target/*.zip* $(ROOT)/target/*.tar*

.PHONY: deploy

deploy:
	echo "Deploying"
	rsync -auv "$(PACKAGES)" "$(WEBSITE)"


