### Suggested Exercise \#1. Solution  

---

**Question**: How many index nodes will fit in a block?  
**Answer**: INDEX_NODE_SIZE = 64 (bytes), that is why only one index-node will fit in a block of size 64 bytes.  

---

**Question**: How many directory entries will fit in a block?  
**Answer**: DIRECTORY_ENTRY_SIZE = 16 (bytes), so 64 / 16 = 4 directory entries will fil in a block of size 64 bytes.  

---

**Question**: Use dump to examine the file system backing file, and note the value in byte 64. What does this value represent?  
**Answer**:  
У другому блоці (що починається з 64 байта, а закінчується 127 байтом включно) знаходиться free list bitmap. 1 біт в free list bitmap відповідає одному data блоку. У першому data блоці знаходиться кореневий каталог, тому в free list bitmap біт з індексом 0 (цей біт відповідає першому data блоку) має значення true (1), що означає, що перший data блок зайнятий. Наразі всі інші data блоки вільні (бо ми ще нічого не додавали в файлову систему), тому в free list bitmap лише біт з індексом 0 має значення true (1). Якщо перетворити free list bitmap в decimal, то якраз і отримаємо одиницю (1) (0...0001 -> 3).  

Free list bitmap - структура, яка для кожного data блоку зберігає інформацію про те, чи цей data блок вільний або зайнятий.  

---

**Question**: Use mkdir to create a directory (e.g., /usr), and then use dump to examine byte 64 again. What do you notice?  
**Answer**:  
Тепер у 64 байті знаходиться значення 3. Було створено каталог "/usr". Цей каталог зайняв один data блок (а саме другий data блок), а тому в free list bitmap біт з індексом 1 (цей біт відповідає другому data блоку) тепер має значення true (1), що означає, що другий data блок тепер зайнятий. Якщо тепер перетворити free list bitmap в decimal, то якраз і отримаємо трійку (3) (0...0011 -> 3).  

Окрім одного data блоку каталог "/usr" ще зайняв один index-node блок (бо для каталогу "/usr" було створено одну index-node, а при поточних параметрах файлової системи (8 блоків по 64 байти) один блок може містити лише одну index-node).  

---

**Question**: Repeat the process of creating a directory (e.g., /bin, /lib, /var, /etc, /home, /mnt, etc.) and examining with dump. How many directories can you create before you fill up the file system? Explain why.  
**Answer**:  
Кожен каталог займає один data блок. Наразі файлова система містить 2 каталоги: кореневий каталог та каталог "/usr". Всього у файловій системі при поточних парамерах 3 data блоки. Перший data блок зайнятий кореневим каталогом, другий data блок - каталогом "/usr", а третій data блок - вільний. Отже, є місце для створення лише одного каталога. Наприклад, створимо каталог "/lib". Цей каталог займе третій data блок. Місця для створення ще одного каталога вже немає.  

Треба уточнити:  
1. Чи може каталог займати кілька (> 1) data блоків? Напевно, так, бо в каталозі може бути нескінченна кількість directory entries. Це правильна відповідь?
2. Чи може в data блоці бути більше ніж один каталог? Тобто одна частина data блоку містить один каталог, а інша частина цього ж data блоку містить інший каталог. Чи може таке бути?
3. Ті ж самі питання (1. та 2. вище) стосовно звичайних файлів.

---

### Вопросы:  
1. Каталоги размещены в оперативной памяти или на диске?
2. Каталог всегда занимает один data блок? Почему? Может ли, например, каталог 1 занимать одну половину data блока, а каталог 2 другую половину этого же data блока?