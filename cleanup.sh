#!/bin/bash

# Cleanup script to remove redundant documentation files
# Keep only: README.md, BUILD.md, API.md, DEVELOPMENT.md, schema.sql

echo "🧹 Cleaning up redundant documentation files..."
echo ""

FILES_TO_DELETE=(
  "README_SECURITY.md"
  "SECURITY_COMPLETE.md"
  "SECURITY_IMPLEMENTATION.md"
  "SECURITY_SUMMARY.md"
  "SECURITY_QUICK_REFERENCE.md"
  "SECURITY_ARCHITECTURE_DIAGRAMS.md"
  "SECURITY_IMPLEMENTATION_INDEX.md"
  "BUILD_AND_RUN.md"
  "GRADLE_MIGRATION.md"
  "GRADLE_QUICK_REFERENCE.md"
  "GRADLE_QUICKSTART.md"
  "DATABASE_MIGRATION.md"
  "QUICKSTART.md"
  "REMINDER_QUICKSTART.md"
  "DYNAMODB_SETUP.md"
  "DYNAMODB_MAPSTRUCT_GUIDE.md"
  "REMINDER_API_DOCS.md"
  "REMINDER_FEATURE_SUMMARY.md"
  "REMINDER_FUNCTIONALITY.md"
  "PROJECT_INIT.md"
  "CHECKLIST.md"
  "IMPLEMENTATION_CHECKLIST.md"
  "COMPLETION_SUMMARY.md"
  "PROJECT_REVIEW_AND_FIXES.md"
  "CLAUDE_QUICK_REFERENCE.md"
  "CLAUDE_REFACTORING_SUMMARY.md"
  "INDEX.md"
  "REVIEW_VERIFICATION_REPORT.md"
  "CODE_REVIEW_SUMMARY.md"
  "FIXES_CHECKLIST.txt"
  "FIXES_SUMMARY.md"
  "INITIALIZATION_REPORT.txt"
)

DELETED=0
for file in "${FILES_TO_DELETE[@]}"; do
  if [ -f "$file" ]; then
    rm "$file"
    echo "✓ Deleted: $file"
    ((DELETED++))
  fi
done

echo ""
echo "✅ Cleanup complete! Deleted $DELETED files"
echo ""
echo "Remaining documentation:"
echo "  • README.md - Main documentation"
echo "  • BUILD.md - Build & deployment"
echo "  • API.md - API reference"
echo "  • DEVELOPMENT.md - Development guide"
echo "  • schema.sql - Database schema"

