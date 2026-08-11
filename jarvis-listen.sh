#!/data/data/com.termux/files/usr/bin/bash

echo "JARVIS wake-word listener started. Say 'jarvis' followed by your command."

while true; do
    TEXT=$(termux-speech-to-text 2>/dev/null)

    if [ -z "$TEXT" ]; then
        continue
    fi

    LOWER=$(echo "$TEXT" | tr '[:upper:]' '[:lower:]')

    if [[ "$LOWER" == *"jarvis"* ]]; then
        COMMAND=$(echo "$LOWER" | sed 's/.*jarvis//')

        if [ -z "$COMMAND" ]; then
            COMMAND="hello"
        fi

        echo "Heard: $COMMAND"

        RESPONSE=$(curl -s "http://localhost:8080/ai?message=$(echo "$COMMAND" | sed 's/ /%20/g')")

        echo "JARVIS: $RESPONSE"

        termux-tts-speak "$RESPONSE"
    fi
done
