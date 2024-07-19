# School-Problem-Manager
A manager for little C++ school homework projects

```
spm help
```

```
spm create my_projects_name 4-04 full_name_oh_yeaaah
spm c name 10-10

commands:
  - help,    h  ()                            # outputs this text
  - create,  c  (name!, date!, [full name])   # creates a new project (full name is purely optional)
  - open,    o  (name)                        # opens a project's directory in VsCode
  - compile, cp (name)                        # compiles the project and adds it to a date's zip
  - list,    l  ()                            # lists all projects
  - mark,    m  (name)                        # marks a project as done
  - send        ()                            # marks a project as sent & opens file explorer with the file.
  - remove,  r  (name!)
  - backup   b  (command!, [backup file name]) # backs-up the current problems.csv or restores it
                                                * commands: {make, load, open} `open` opens in file explorer

The data for this program is stored either in the same directory as the .jar is, or inside the .jar!

@REM Argument types: "[optional argument]", "possibly implied argument", "required argument!".
@REM If you get an exception, you, most likely, did something wrong, please read the first line of it.
@REM Dates are just a way to group projects. My (personal) format: MM-DD; M -- Month, D -- Day
```
