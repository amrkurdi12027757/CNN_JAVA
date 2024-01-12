
# AI Second Homework
This project aims to showcase the implementation of Neural Networks within the realm of Artificial Intelligence. It explores diverse configurations of hidden layer perceptrons and activation functions. The project is equipped with a script that streamlines the generation of data, facilitating subsequent training and testing procedures
## Features

- **Demo Data Generation:** Utilize the `GenerateFruitDataset` class under the `com.example.cnn_java.datapackage` package to generate demo data.

- **Gradient Descent Visualization:** Graphically represent the gradient descent per epoch, providing a visual insight into the optimization process.

- **Testing Data Evaluation:** Visualize the accuracy of the testing data through a colored graph, where points appear in a saturated color if their prediction matches their real value.

- **Versatile Data Handling:** The application accommodates any dataset with a single categorical output, such as the fruit example. Inputs can be either categorical or numerical.

- **Configurable Neural Network Architecture:** While the app's backend supports an arbitrary number of layers and perceptrons, the user interface allows the specification of the number of perceptrons in the hidden layer, limiting it to three layers.

- **Training Control:** Choose between setting a goal or an epoch count for training. The app will train the model until the specified goal is achieved, providing flexibility in training duration.

- **Import Datasets:** Easily import datasets into the application for seamless integration and analysis.

- **Custom Activation Functions:** Declare new activation functions effortlessly by implementing the `Activation` interface. The app dynamically incorporates these functions, offering users the option to select them through the UI.

- **Adjustable Parameters:**
  - Modify the split ratio of the data.
  - Fine-tune the learning rate.
  - Select from a variety of activation functions.

- **Cross-Platform Compatibility:** Built using JavaFx, the application ensures cross-platform usability, allowing users to access and use it seamlessly across different operating systems.
## Run Locally

To run the project locally make sure to have JDK17 installed, then follow these steps:

1. **Clone the Repository:**
   Ensure that Git is installed on your machine. Clone the project repository by executing the following command in the terminal:

    ```bash
    git clone https://github.com/amrkurdi12027757/CNN_JAVA
    ```

2. **Navigate to Project Directory:**
   Change your working directory to the project folder:

    ```bash
    cd CNN_JAVA
    ```

3. **Install Maven:**
   Make sure Maven is installed on your machine or use `mvnw` instead of `mvn`. If not, you can install it from [Maven's official website](https://maven.apache.org/install.html).

4. **Build and Run:**
   Execute the following Maven command in the terminal to clean, install dependencies, and run the application:

    ```bash
    mvn clean install exec:java
    ```

   This command will handle the necessary build steps and launch the application. Ensure that the process completes without errors.

Now, the application should be running locally on your machine. Access it through the appropriate UI or interface, as specified by the application documentation.
## Authors

- [@amrkurdi12027757](https://github.com/amrkurdi12027757)