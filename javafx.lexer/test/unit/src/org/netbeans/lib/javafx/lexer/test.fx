import javafx.ui.*;


        class HelloWorldModel {
            attribute saying: String;
        }

        var model = HelloWorldModel {
            saying: "Hello World"
        };

        Frame {
            title: "Hello World JavaFX"
            width: 200

            content: Label {
                text: bind model.saying
            }
            visible: true
        };