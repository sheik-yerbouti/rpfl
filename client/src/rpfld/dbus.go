package rpfld

import (
	"github.com/godbus/dbus"
	"fmt"
	"os"
	"github.com/godbus/dbus/introspect"
)

const intro = `
<node>
	<interface name="com.github.guelfey.Demo">
		<method name="Foo">
			<arg direction="out" type="s"/>
		</method>
	</interface>` + introspect.IntrospectDataString + `</node> `

type foo string

func (f foo) Foo() (string, *dbus.Error) {
	fmt.Println(f)
	return string(f), nil
}

func ExportDbusInterface(){
	conn, err := dbus.SessionBus()
	if err != nil {
		panic(err)
	}
	reply, err := conn.RequestName("org.rpfl.rpfld",
		dbus.NameFlagDoNotQueue)
	if err != nil {
		panic(err)
	}
	if reply != dbus.RequestNameReplyPrimaryOwner {
		fmt.Fprintln(os.Stderr, "name already taken")
		os.Exit(1)
	}
	f := foo("Bar!")
	conn.Export(f, "org/rpfl", "org/rpfl/rpfld")
	conn.Export(introspect.Introspectable(intro), "org/rpfl/rpfld",
		"org.freedesktop.DBus.Introspectable")
	fmt.Println("Listening on org/rpfl / org/rpfl/rpfld")
}
