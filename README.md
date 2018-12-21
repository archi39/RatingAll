<p align="center"><img src=".gitimage/logo_frame_text.png" height="200" width="200"></p>

---

<h1 align=center>Краткая анотация:</h1>

<h2>О чём проект</h2>

<p>Главная цель - приложение (обучение меня азам android разработки), которое позволит 
формировать списки вещей с указанием своего собственного (субъективного) мнения об этих вещах 
(рейтинги, респекты - как ни назови - всё едино). Желательно (обязательно) сделать приложение 
удобным и интуитивно понятным. </p>

***

<h2>  Roadmap:  </h2>

<dl>
    <dt><b> Оснастка: </b></dt>
    <dd>
        <div><div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> Подключить базу данных (далее - БД) к проекту</div>
            <div> <ul>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Перейти на использование RecyclerView </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Реализовать изменение "рейтинга" заново </li>                
            </ul></div>
        </div>
        <div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> 
            Обеспечить возможность добавления записей в БД</div>
        <div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> 
            Обеспечить возможность удаления записей в БД</div>
        <div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> 
            Обеспечить возможность редактирования записей в БД</div>
        <div><div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> Разобраться с подключением сторонних библиотек из GitHub Fork репозиториев</div>
            <div> <ul>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Разобраться с использованием JitPack </li>                
            </ul></div>
        </div>
        <div><img src=".gitimage/chkbx_unchecked.png" height="14" width="14"> 
                    Добавить возможность фильтрации по тегам</div>
            <div> <ul>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Доработать архитектуру базы данных, для хранения тэгов </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Обновить базу без потери данных пользователя </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Настроить корректное отображение тэгов на экране </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Добавить возможность добавления тэгов </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Добавить возможность редактирования тэгов 
                    <ul><li><s>Реализовать перезагрузку данных из базы в основном окне, после выхода из активити тэга</s></li>
                    <li><s>Реализовать редактирование на базе окна диалога</s></li></ul>
                </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Добавить возможность удаления тэгов 
                    <ul><li><s>Реализовать безопастное удаление тэга, учитывая связанные таблицы</s> (PS: походу SQLite сам реализует безопастное удаление)</li></ul>
                </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Переписать окно добавления элементов с возможностью выставления тэгов
                    <ul><li><s>Добавить диалоговое окно со списком уже существующих тэгов</s></li>
                    <li><s>Добавить возможность привязки тэгов к элементу</s></li></ul>
                </li>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Переписать окно редактирования элементов с возможностью выставления тэгов
                    <ul><li><s>Добавить диалоговое окно со списком уже существующих тэгов</s></li>
                    <li><s>Добавить возможность привязки тэгов к элементу</s></li></ul>
                </li>
            </ul></div> 
        <div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> Переработать ссылку горизонтального меню "Очистка базы" с учетом новой структуры базы данных приложения</div>
        <div><img src=".gitimage/chkbx_unchecked.png" height="14" width="14"> Реализовать +/- удобную систему логирования для быстрого поиска #BUG`ов </div>
    </dd>
    <dt><b> Визуальные плюхи: </b></dt>
    <dd>
        <div>
            <div><img src=".gitimage/chkbx_checked.png" height="14" width="14"> Сделать возможность удаления swype`ом</div>
            <div> <ul>
                <li> <img src=".gitimage/chkbx_checked.png" height="14" width="14"> Добавить строковый ID для элементов списка </li>                
            </ul></div>
            <div><img src=".gitimage/chkbx_unchecked.png" height="14" width="14"> Продумать и внедрить адекватную систему коммитов для проекта</div>
        </div>    
    </dd>
</dl>

***

<h2>  Отдельный пункт про коммиты:  </h2>

