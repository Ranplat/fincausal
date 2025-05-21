# FinCausal - Financial Text Causal Relation Extraction System

## Project Overview

This project is a Chinese semantic analysis system based on Stanford CoreNLP, focusing on extracting causal relationships from financial domain texts. The system can identify causal relationships, temporal relationships, and perform financial domain-specific adaptations.

## Key Features

1. **Causal Relationship Extraction**
   - Identify causal connections in text
   - Extract causes and effects
   - Support complex multi-causal relationship analysis

2. **Temporal Relationship Processing**
   - Identify chronological order of events
   - Support various temporal markers (before, after, during, etc.)
   - Analyze temporal dependencies between events

3. **Financial Domain Adaptation**
   - Financial terminology recognition and standardization
   - Financial entity classification
   - Professional terminology explanation

## System Architecture

The system adopts a pipeline processing architecture, consisting of the following components:

1. **Text Preprocessor**
   - Text cleaning and standardization
   - Special character handling
   - Chinese full/half-width conversion

2. **NLP Parser**
   - Based on Stanford CoreNLP
   - Word segmentation and POS tagging
   - Syntactic parsing and dependency analysis

3. **Causal Relationship Extractor**
   - Rule and pattern-based causal relationship recognition
   - Causal relationship confidence assessment

4. **Temporal Processor**
   - Temporal marker recognition
   - Event temporal relationship analysis

5. **Financial Domain Adapter**
   - Financial terminology dictionary
   - Domain-specific rule processing
   - Term classification and explanation

## Usage

### Requirements
- Java 8 or higher
- Maven 3.x

### Installation
1. Clone the project code
2. Install dependencies using Maven:
   ```bash
   mvn install
   ```

### Running
```bash
# Windows
autotest.bat

# Linux/Mac
./autotest.sh
```

Or directly run the JAR file:
```bash
java -jar causal-relation-extraction.jar <input_file_path> [output_file_path]
```

### Configuration
System configuration file is located at `src/main/resources/config.properties`, main configuration items include:
- NLP language settings
- Preprocessing options
- Causal relationship extraction parameters
- Financial dictionary path

## Test Cases

The project includes multiple test case sets:
- Basic causal relationship tests
- Financial terminology recognition tests
- Temporal relationship tests
- Complex causal relationship tests

## License

This project is licensed under the [GNU Lesser General Public License v3.0](https://www.gnu.org/licenses/lgpl-3.0.html).

## Contributing

Issues and Pull Requests are welcome to help improve the project. Before submitting code, please ensure:
1. Code complies with project coding standards
2. Appropriate unit tests are added
3. All test cases pass
4. Related documentation is updated
