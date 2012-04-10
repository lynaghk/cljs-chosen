#!/bin/bash

#Just a little build script that'll build the JAR and install to your local maven repo.

set -e

CHOSEN=vendor/chosen/chosen

cp $CHOSEN/chosen.jquery.min.js resources/

lein2 install
