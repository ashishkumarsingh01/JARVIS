#!/data/data/com.termux/files/usr/bin/bash

echo "🎤 JARVIS Voice Control"
echo "Say a command..."

while true
do
    message=$(termux-speech-to-text)

    if [ -z "$message" ]; then
        echo "No speech detected."
        continue
    fi

    echo "You: $message"

    response=$(curl -s -G \
        --data-urlencode "message=$message" \
        "http://localhost:8080/ai")

    echo "JARVIS: $response"
    echo

    termux-tts-speak "$response"

    echo "🎤 Say another command..."
done
