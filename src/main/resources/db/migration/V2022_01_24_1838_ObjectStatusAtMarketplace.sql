alter table objects modify status enum('IN_SPACE', 'ATTACHED_TO', 'IN_CONTAINER', 'NOT_DEFINED', 'AT_MARKETPLACE') default 'NOT_DEFINED';