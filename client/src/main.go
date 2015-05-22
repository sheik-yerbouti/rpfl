package main

import (
	"rpfld"
)

var running bool

func main() {
	if running {
		panic("")
	}

	rpfld.InitSystem()
	rpfld.ExportDbusInterface()
	select {}
}
