from py4j.java_gateway import JavaGateway, CallbackServerParameters

from midi_manipulation import midi_to_note_state_matrix, \
    note_state_matrix_to_midi


class PythonMidiImpl(object):

    def __init__(self):
        self.gateway = None

    def read(self, file):
        print(f"reading {file}")
        res = midi_to_note_state_matrix(file)
        print(f"{file} read, convert to java")
        rows = len(res)
        cols = len(res[0])

        array_type = self.gateway.jvm.int
        java_arr = self.gateway.new_array(array_type, rows, cols)
        for i in range(0, rows):
            for j in range(0, cols):
                # java_arr[i][j] = float(res[i][j])
                java_arr[i][j] = res[i][j]
        return java_arr

    def write(self, java_arr, file):
        data = []
        for i in range(0, len(java_arr)):
            tmp = []
            for j in range(0, len(java_arr[i])):
                tmp.append(java_arr[i][j])
            data.append(tmp)
        return note_state_matrix_to_midi(data, name=file)

    def test(self, s):
        int_class = self.gateway.jvm.int
        arr = self.gateway.new_array(int_class, len(s))
        for i in range(0, len(s)):
            arr[i] = i
        return arr

    class Java:
        implements = ["rbm.midi.PythonMidiReader"]


if __name__ == "__main__":

    gateway = None
    try:
        python_impl = PythonMidiImpl()
        gateway = JavaGateway(
            callback_server_parameters=CallbackServerParameters(),
            python_server_entry_point=python_impl)

        python_impl.gateway = gateway

        # gateway.start_callback_server()
    except KeyboardInterrupt:
        if gateway is not None:
            gateway.shutdown()

