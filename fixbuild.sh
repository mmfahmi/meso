#!/bin/bash

# Set the base Java source directory
SRC_DIR="./app/src/main/java/com/socialmediasafety"

echo "🔧 Fixing incorrect Platform imports..."
# Fix import for Platform in all .java files
find "$SRC_DIR" -type f -name "*.java" -exec sed -i 's/import com\.socialmediasafety\.rating\.analysis\.Platform;/import com.socialmediasafety.rating.Platform;/' {} +

echo "✅ Platform imports fixed."

# RiskLevel file path (adjust if necessary)
RISK_LEVEL_FILE="$SRC_DIR/rating/analysis/RiskLevel.java"

if [[ -f "$RISK_LEVEL_FILE" ]]; then
  echo "🔧 Rewriting RiskLevel enum to use simple constants..."
  cat <<EOF > "$RISK_LEVEL_FILE"
package com.socialmediasafety.rating.analysis;

public enum RiskLevel {
    SAFE,
    LOW,
    MEDIUM,
    HIGH
}
EOF
  echo "✅ RiskLevel enum rewritten."
else
  echo "❌ RiskLevel.java not found at $RISK_LEVEL_FILE"
fi
