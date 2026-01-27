#!/bin/bash
# Save as test_passwords2.sh

passwords=(
    "itsmetarikov@3002"
    "itsme@123"
    # Without special characters
    "chess"
    "chessgame"
    "chessGame"
    "chessmaster"
    "chessMaster"
    
    # Just numbers
    "000000"
    "111111"
    "1234567890"
    "0123456789"
    
    # Year variations
    "chess2023"
    "chess2024"
    "chess2025"
    "chessgame2023"
    "chessgame2024"
    "chessgame2025"
    "Chess2023"
    "Chess2024"
    "Chess2025"
    
    # Different number patterns
    "chess01"
    "chess001"
    "chess!123"
    "chess#123"
    "chess$123"
    "chess321"
    "chess456"
    "chess789"
    
    # Tarikov variations
    "tarikov"
    "tarikov123"
    "tarikov@123"
    "tarikov1234"
    "Tarikov123"
    "Tarikov@123"
    
    # Simple patterns
    "qwerty"
    "qwerty123"
    "password123"
    "Password123"
    "admin"
    "admin123"
    "test123"
    "test@123"
    
    # Keystore specific
    "keystore"
    "keystore123"
    "keyStore123"
    "keystorepassword"
    "mykey"
    "mykey123"
    "myKey123"
    
    # Date patterns
    "01012023"
    "01012024"
    "01012025"
    
    # Letter + number simple
    "a123456"
    "abc123"
    "abc1234"
    
    # Reversed
    "321ssehc"
    "321emagssehc"
    
    # Double words
    "chesschess"
    "chess123chess"
    
    # Common substitutions
    "ch3ss123"
    "ch3ssG@m3"
    "ch355"
    "ch355g@m3"
    
    # Short simple ones
    "1234"
    "12345"
    "123456"
    "1234567"
    "12345678"
    "123456789"
    
    # Release/debug related
    "release"
    "release123"
    "debug"
    "debug123"
    
    # Android Studio defaults
    "android123"
    "Android123"
    "studio"
    "studio123"
)

for pass in "${passwords[@]}"; do
    echo "Trying: $pass"
    echo "$pass" | keytool -list -keystore keyStore.jks -storepass "$pass" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "SUCCESS! Password is: $pass"
        exit 0
    fi
done

echo "None of the passwords worked"
