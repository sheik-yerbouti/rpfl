package rpfld
import (
	"io/ioutil"
	"os"
	"encoding/xml"
)

var endpoints []Endpoint

func InitSystem(){
	files, _ := ioutil.ReadDir("/etc/rpfl.trustees.d")
	endpoints = make([]Endpoint, len(files))

	for _, fileInfo := range files {

		file, _ := os.Open(fileInfo.Name())

		var endpoint Endpoint
		if err := xml.NewDecoder(file).Decode(&endpoint); err != nil {
			panic(err)
		}
	}
}
