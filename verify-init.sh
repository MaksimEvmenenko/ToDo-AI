#!/bin/bash

# 🎯 ToDo-AI Project Initialization Checklist
# This script verifies all project files have been created correctly

echo "🔍 Checking ToDo-AI Project Initialization..."
echo "=================================================="
echo ""

PROJECT_DIR="/Users/maksim_evmenenko/Projects/ToDo-AI"
cd "$PROJECT_DIR"

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Counter
TOTAL=0
COMPLETED=0

# Check files
check_file() {
    local file=$1
    local description=$2
    TOTAL=$((TOTAL + 1))

    if [ -f "$file" ]; then
        echo -e "${GREEN}✅${NC} $description"
        COMPLETED=$((COMPLETED + 1))
    else
        echo -e "${RED}❌${NC} $description - MISSING: $file"
    fi
}

check_dir() {
    local dir=$1
    local description=$2
    TOTAL=$((TOTAL + 1))

    if [ -d "$dir" ]; then
        echo -e "${GREEN}✅${NC} $description"
        COMPLETED=$((COMPLETED + 1))
    else
        echo -e "${RED}❌${NC} $description - MISSING: $dir"
    fi
}

echo "📁 Directory Structure:"
echo "---------------------"
check_dir "src/main/java/com/todoai" "Java source directory"
check_dir "src/main/resources" "Resources directory"

echo ""
echo "🔧 Configuration Files:"
echo "----------------------"
check_file "pom.xml" "Maven configuration"
check_file "src/main/resources/application.properties" "Spring Boot properties"

echo ""
echo "📝 Documentation:"
echo "-----------------"
check_file "README.md" "Main documentation"
check_file "QUICKSTART.md" "Quick start guide"
check_file "DEVELOPMENT.md" "Development guidelines"
check_file "PROJECT_INIT.md" "Initialization summary"

echo ""
echo "🤖 Claude Support:"
echo "------------------"
check_file ".claude" "Claude project metadata"
check_file ".claudeignore" "Claude ignore patterns"
check_file ".cursorules" "IDE assistant rules"
check_file ".gitignore" "Git ignore patterns"

echo ""
echo "☕ Java Source Files:"
echo "--------------------"
check_file "src/main/java/com/todoai/ToDoAiApplication.java" "Main application class"
check_file "src/main/java/com/todoai/controller/TodoController.java" "REST Controller"
check_file "src/main/java/com/todoai/service/TodoService.java" "Service layer"
check_file "src/main/java/com/todoai/repository/TodoRepository.java" "Repository layer"
check_file "src/main/java/com/todoai/model/Todo.java" "Entity model"
check_file "src/main/java/com/todoai/dto/TodoDTO.java" "Data Transfer Object"

echo ""
echo "=================================================="
echo -e "Initialization Status: ${GREEN}$COMPLETED/$TOTAL${NC} files created"

if [ $COMPLETED -eq $TOTAL ]; then
    echo -e "${GREEN}✅ PROJECT INITIALIZATION COMPLETE!${NC}"
    echo ""
    echo "🚀 Next Steps:"
    echo "1. Build: mvn clean install"
    echo "2. Run: mvn spring-boot:run"
    echo "3. Test: curl http://localhost:8080/api/v1/todo/list"
    echo ""
    exit 0
else
    echo -e "${RED}❌ SOME FILES ARE MISSING${NC}"
    echo ""
    exit 1
fi

