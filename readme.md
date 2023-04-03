# Android/teachme.io:
<br/>

This android application based on java, aims to help learning english verbs and common tenses.


---

### **Group members:**
<br/>

- Massiles GHERNAOUT.
- Antoine MONTIER.

--- 


### **File architecture:**
<br/>

```sh

    .
    ├── LICENSE
    ├── readme.md
    └── src

    1 directory, 2 files

```

---

### **Projet description and implementation:**

<br/>

As mentionned above, this application will allow the user to choose verbs and then be tested on them. For that, the application will be composed of two views, the main view (start screen), and then the availible tests view. Follow reading below for more details. 


#### *Start screen* : 
The start screen will be a list of all the verbs. The user will have the opportunity to check every verb he wants to learn.

#### *Classic mode* :
After the user has chosen the verbs, he will have the opportunity to learn more about these. For each question, the app will provide one of the following: the **french** version, the **infinitive**, the **preterit** or the **past participle** according to his choice. The remaining options should be filled by the user.

#### *Random mode* :
This mode is almost the same as the *Classic mode*. For each verb, the user will need to provide 3 of the 4 fields (**french**, **infinitive**, **preterit**, **past participle**). The 3 mandatory fields will be chosen randomly.

#### *Improvements* : 
The user answers are going to be stored and computed by this app. The goal is to provide the user with a pertinent question, depending on his previous failures.
