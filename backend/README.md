# proiect-fullstack-PIP

## running backend
mvn spring-boot:run 



# 📚 Generare Documentație Javadoc (Windows)

Acest proiect include un script `.bat` pentru generarea automată a documentației Javadoc a claselor Java, compatibil cu sistemele Windows.

---

## 🔧 Cerințe

- **Java JDK instalat**
    - Verifică rulând în CMD:
      ```cmd
      java -version
      javadoc -version
      ```
    - Dacă nu sunt recunoscute, instalează JDK de la: https://jdk.java.net/

---

## ▶️ Pași pentru generarea documentației

1. Asigură-te că ești în folderul proiectului (acolo unde este scriptul `generate-javadoc.bat`).
   Ex:
   ```cmd
   cd D:\PIP-2025-2026\backend

2. Ruleaza comanda:
   ```cmd
   mvn clean javadoc:javadoc
3. Mergi in target/site/apidocs si ruleaza
   ```cmd
   start index.html


