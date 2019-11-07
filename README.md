# spotter

Exports a list of your liked songs to /tmp/spotify-liked.csv.

## Building/Running

    # mvn clean package
    # export SPOTIFY_CLIENT_ID=...
    # java -jar target/spotter-1.0-SNAPSHOT.jar

Inspect the export via:

    # column -s '<Ctrl+v><Tab>' -t /tmp/spotify-liked.csv
