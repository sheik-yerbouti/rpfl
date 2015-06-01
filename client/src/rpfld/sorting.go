package rpfld

type ByUrl []DownloadedResource

func (s ByUrl) Len() int {
	return len(s)
}
func (s ByUrl) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}
func (s ByUrl) Less(i, j int) bool {
	return *s[i].url < *s[j].url
}
