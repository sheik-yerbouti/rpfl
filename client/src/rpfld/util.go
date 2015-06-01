package rpfld

func getUrls(downloadedResources []DownloadedResource)(urls []string){
	urls = make([]string, len(downloadedResources))

	for _, downloadedResource := range downloadedResources {
		urls = append(urls, *downloadedResource.url)
	}

	return urls
}
