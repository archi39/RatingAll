<p align="center"><img src=".gitimage/logo_frame_text.png" height="200" width="200"></p>

---
> проект заморожен, так как практически всё свободное время уходит на обучение и разработку "рабочих проектов"

> Однако я не бросаю попытки сделать его более красивым, а код более читаемым (возвращаюсь к проекту по мере свободного времени): сейчас параллельно осваиваю основы прототипирования, в частности Figma: [разрабатываю новый дизайн для проекта, под впечатлением GoogleMaterialDesign](https://github.com/EvgenySamarin/RatingAll-Figma)

<h1 align=center>Краткая анотация:</h1>

<h2>О чём проект</h2>

<p>Главная цель - приложение (обучение меня азам android разработки), которое позволит 
формировать списки вещей с указанием своего собственного (субъективного) мнения об этих вещах 
(рейтинги, респекты - как ни назови - всё едино). Желательно (обязательно) сделать приложение 
удобным и интуитивно понятным. </p>
<p><a href='https://play.google.com/store/apps/details?id=com.dogvscat.retingall'>Ссылка на приложение в Google Play Market</a></p>
***
<h2>Проект придерживается Agile системы Kanban</h2>
<p>Процесс разработки ориентирован на выполнение одной конкретной задач. Глупо выделять спринт если разрабочик всего 1 🤷‍♂️</p>
<h2>Дорожная карта проекта, в силу постоянной пополняемости переехала на Trello</h2>
<p><a href="https://trello.com/b/Kwll6VHA">https://trello.com/RatingAll</a></p>
***

<h2>Про нумерацию версий</h2>

<div>Проект придерживается следующей системы нумерации <b>R.M.m.B</b>, где:</div>
<ul>
  <li>R - Release, целое число номер версии приложения, меняется в случае крупного редизайна, изменения архитектуры и т.д.</li>
  <li>M - Major update, целое число крупные изменения, добавление новых функций</li>
  <li>m - minor update, целое число незначительные изменения в программе, рефакторинг кода, оптимизация, мелкий редизайн и т.д.</li>
  <li>B - Bugfix, целое число исправление ошибок</li>
</ul>
<div>Пример версии <b>ver. 1.0.1.5</b> - первый выпуск программы, в котором проведены незначительные изменения и поправлены 5 ошибок</div>
<p>В случае если в значении <b>B</b> набирается больше 25 исправлений ошибок, повышается значение <b>M</b>, значения <b>m</b> и <b>B</b> обнуляются</p>
<p>В случае если в значении <b>m</b> набирается больше 9 изменений, повышается значение <b>M</b>, значения <b>m</b> и <b>B</b> обнуляются</p>
<p>В случае если в значении <b>M</b> набирается больше 9 изменений, повышается значение <b>R</b>, значения <b>M</b>, <b>m</b> и <b>B</b> обнуляются</p>

***

<h2>  Отдельный пункт про коммиты:  </h2>

<p>Проект использует VCS <s>да ладно</s>, на 21.12.2018 коммиты в проекте слабо читаемы и мало-логичны
- в следствии чего, в будующем могут возникнуть проблемы с чтением "истории", поэтому впредь
следует придерживаться одной определенной системы применения коммитов:</p>
отличный GUIDline описан в статье <a href="https://gist.github.com/robertpainsi/b632364184e70900af4ab688decf6f53">https://gist.github.com/robertpainsi/</a>
Основная идея: "<b>Что сделать</b> + <b>для какой сущности</b> + <b>подробности (необязательно)</b>"
Каждый commit начинается с префикса
<div>Система префиксов:
<ul>
<li><b>#FTR</b> - (сокр. feature) - добавление нового функционала</li>
<li><b>#FIX</b> - исправление ошибки</li>
<li><b>#RFT</b> - (сокр. refactoring) изменение кода</li>
<li><b>#DOC</b> - (сокр. documentation) добавление описаний и анотаций</li>
<li><b>#TST</b> - (сокр. testing) написание тестов, тестирование кода</li>
</ul>

префиксы могут дополняться по мере необходимости.
</div>
