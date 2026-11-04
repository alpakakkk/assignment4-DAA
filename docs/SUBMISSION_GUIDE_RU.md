# Сдача проекта Patient Zero — пошаговая инструкция

## А. Запусти и проверь (в корневой папке `patient-zero`)

1. Убедись, что установлен **JDK 17+**, а не только JRE: `java -version` и `javac -version`.
2. Создай **свои** четыре входных файла: `java tools/MakeVariant.java ТВОЙ_ID_ИЗ_MOODLE data`. Важно: нули в начале ID сохраняются.
3. Запусти преподавательскую проверку: `java tools/Check.java` — должны пройти `tiny`, `small`, `dense`, по 9 правильных строк каждый.
4. Если надо, скомпилируй вручную: `javac -encoding UTF-8 -d out src/*.java`.
5. Запусти основной код: `java -cp out Main` и сохрани **все 9 строк** в `README.md`.
6. Открой `output/outbreak.html` двойным щелчком в браузере. Убедись, что все девять чисел с зелёными галочками, затем сохрани **скриншот целой страницы** в `output/outbreak.png`.
7. Замерь время: `java -cp out Bench`, перенеси данные в таблицу Timing в `README.md`.
8. Необязательно, но полезно: `javac -cp out -d out tools/ExtraTests.java` и `java -cp out ExtraTests`.

## Б. Доработай отчёт

В `README.md` заполнить студенческий ID, имя и группу; **заменить демонстрационные результаты** собственными; заменить скриншот; уточнить измерения времени. Измени объяснения вопросов по пяти миссиям так, как ты действительно понимаешь их, и сохрани правдивую запись об AI use. Проверь таблицу сложности и раздел Before you submit.

## В. GitHub

В архиве Git-история создана **AI для иллюстрации последовательности миссий**. Не выдавай эти коммиты за написанные тобой; после собственного изучения и редактирования сделай реальные новые коммиты. Если `.git/` отсутствует из-за особенностей распаковки, можно начать заново: `git init`, `git branch -M main`.

Проверить историю и изменения:

```bash
git log --oneline
git status
git diff
```

Укажи **своё** имя и email для новых коммитов:

```bash
git config user.name "YOUR NAME"
git config user.email "YOUR_EMAIL@example.com"
```

Если работаешь по миссиям/правишь методы отдельно, после каждого значимого изменения:

```bash
git add src/Strains.java
git commit -m "Explain and refine edit distance implementation"
git add -A
git commit -m "Complete personalized README and outbreak screenshot"
```

(Примерные команды: меняй файлы до коммита, иначе Git может сообщить `nothing to commit`.) Создай новый **public** репозиторий `patient-zero` на GitHub, скопируй **его реальный URL**, затем:

```bash
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/patient-zero.git
git push -u origin main
```

Если `origin` уже есть: `git remote -v`; при необходимости изменяй его с `git remote set-url origin ...`. GitHub может запросить вход через браузер, token или Git Credential Manager. Для первоначальной публикации **своих** файлов можно использовать GitHub Desktop.

## Г. Что сдавать

В Moodle по условию нужен **ссылка на публичный GitHub-репозиторий**, а не ZIP. Проверь в приватном окне браузера, что `README.md`, `src/`, `data/`, `output/outbreak.png` доступны по ссылке. Сделай это до дедлайна, указанного именно в Moodle.

## Д. Короткая подготовка к quiz (50 баллов)

20 вопросов, 30 минут: 3 простых вопроса по условиям, 3 вычисления рукой, 14 вопросов по теории DP, edit distance, LCS, pi/KMP, Rabin-Karp и DP на дереве. Самостоятельно реши пример `examples/tiny`, затем прочитай `docs/Assignment4_defense_prep.pdf` и открой анимации `output/outbreak.html`. Во время quiz использование AI не разрешено.
