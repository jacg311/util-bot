command -v curl >/dev/null 2>&1 || { echo >&2 "I require 'curl' but it's not installed.  Aborting."; exit 1; }
command -v jq >/dev/null 2>&1 || { echo >&2 "I require 'jq' but it's not installed.  Aborting."; exit 1; }

url="https://api.github.com/repos/jacg311/util-bot/releases/latest"

release=$(curl -s $url | jq '.assets[]|select(.name|endswith("-all.jar"))')
name=$(jq -r '.name' <<< $release)
url=$(jq -r '.browser_download_url' <<< $release)
curl -L -O $url

java -jar $name