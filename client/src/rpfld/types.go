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

type VerificationProcess struct {
	payload []byte
	downloadedResources []DownloadedResource
	verifications chan Verification
}

type DownloadedResource struct {
	path *string
	url *string
}

type Verification struct {
	endpoint *Endpoint
	signature [64]byte
}