package rpfld
import (
	"net/url"
	"encoding/xml"
)

type Endpoint struct {
	xmlName xml.Name `xml:"endpoint"`
	url url.URL `xml:url`
	publicKey [32]byte
}

type DownloadedResource struct {
	path string
}
