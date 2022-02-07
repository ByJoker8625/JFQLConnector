package de.byjoker.jfql.repository

import de.byjoker.jfql.util.TableType

data class TableData(val name: String, val primary: String, val structure: String, val type: TableType)
